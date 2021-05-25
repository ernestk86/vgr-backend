package com.vgrental.services;

import java.time.LocalDate;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.vgrental.models.Game;
import com.vgrental.models.Rental;
import com.vgrental.models.User;
import com.vgrental.repositories.GameDAO;
import com.vgrental.repositories.RentalDAO;
import com.vgrental.repositories.UserDAO;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Service
public class RentalService {
	
	private static final Logger log = LoggerFactory.getLogger(RentalService.class);
	private MeterRegistry meterRegistry;
	private Counter failedConnectionAttempts;
	private Counter successfulConnectionAttempts;
	private Counter emailSent;
	private static final String CONNECTIONATTEMPT = "connection_attempt";
	private static final String TYPE = "type";
	private static final String SUCCESS = "success";
	private static final String FAIL = "fail";
	private static final String EMAIL = "email";
	private static final String SENT = "sent";
	
	public RentalService (MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        successfulConnectionAttempts = meterRegistry.counter(CONNECTIONATTEMPT, TYPE, SUCCESS);
		failedConnectionAttempts = meterRegistry.counter(CONNECTIONATTEMPT, TYPE, FAIL);
		emailSent = meterRegistry.counter(EMAIL, TYPE, SENT);
    }
	
	@Autowired
	private RentalDAO rentalDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private GameDAO gameDAO;
	
	public Rental insert(int userId, int gameId) {
		MDC.put("userId", Integer.toString(userId));
		MDC.put("gameId", Integer.toString(gameId));
		// Find user and game
		User u = null;
		Game g = null;
		
		try {
			u = userDAO.findById(userId).orElse(null);
			successfulConnectionAttempts.increment(1);
			g = gameDAO.findById(gameId).orElse(null);	
			successfulConnectionAttempts.increment(1);
		} catch (DataAccessException e) {
			failedConnectionAttempts.increment(1);
		}
		
		if(u == null) {
			log.error("Unable to find user");
			MDC.clear();
		}
		
		if(g == null) {
			log.error("Unable to find game");
			MDC.clear();
		}
		
		// Make sure user and game exists and game is available
		if(u != null && g != null && g.isAvailable()) {
			Rental r = new Rental(u, g);
			Rental newRental = null;
			g.setAvailable(false);
			
			try {
				gameDAO.save(g);
				successfulConnectionAttempts.increment(1);
				newRental = rentalDAO.save(r);
				successfulConnectionAttempts.increment(1);
			} catch (DataAccessException e) {
				failedConnectionAttempts.increment(1);
			}
			
			MDC.put("insert", r.toString());
			log.info("New rental inserted");
			MDC.clear();
			
			return newRental;
		}
		
		log.error("Unable to find game");
		MDC.clear();
		return null;
	}
	
	public boolean deleteById(int gameId) {
		MDC.put("gameId", Integer.toString(gameId));
		try {
			rentalDAO.deleteById(gameId);
			successfulConnectionAttempts.increment(1);
			log.info("Game has been successfully returned");
			MDC.clear();
			Game g = gameDAO.findById(gameId).orElse(null);
			successfulConnectionAttempts.increment(1);
			if(g != null) {
				g.setAvailable(true); // Make game available
				gameDAO.save(g);
				successfulConnectionAttempts.increment(1);
				log.info("Game has been set to available");
				MDC.clear();
				return true;
			}
			
			log.error("For whatever reason game g is null");
			MDC.clear();
			return false;
		} catch(DataAccessException e) {
			log.error("Can't delete a rental for a game that doesn't exist", e);
			MDC.clear();
			failedConnectionAttempts.increment(1);
			return false;
		}
	}
	
	public void sendEmail(Rental r) {
		// Grab user's email
		User u = userDAO.findById(r.getUser().getId()).orElse(null);
		if(u == null) {
			log.error("User doesn't exist for the overdue rental");
			return;
		}
		Game g = gameDAO.findById(r.getGame().getId()).orElse(null);
		if(g == null) {
			log.error("Game doesn't exist for the overdue rental");
			return;
		}
		// String destinationEmail = u.getEmail();
		String destinationEmail = "ernest.kim@revature.net";
		
		// Vendor's email
		String senderEmail = "ernest.kim@revature.net";
		
		// SMTP Service login credentials
		String uname = System.getenv("AWS_SES_USERNAME");
		String pwd = System.getenv("AWS_SES_PASSWORD");
		String smtphost = System.getenv("AWS_SES_HOST");

		// Properties for session
		Properties propvls = new Properties();
		propvls.put("mail.smtp.auth", "true");
		propvls.put("mail.smtp.starttls.enable", "true");
		propvls.put("mail.smtp.host", smtphost);
		propvls.put("mail.smtp.port", "587");
		
		// Session for MimeMessage
		Session sessionobj = Session.getInstance(propvls, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(uname, pwd);
			}
		});
		
		// Subject and Body of email
		String username = u.getUsername();
		String dueDate = r.getDueDate().toString();
		String gameName = g.getName();
		String console = g.getConsole().toString();
		String subject = "Your video game rental is overdue!";
		String body = username + ", your video game rental is now overdue.\n"
				+ "Please return " + gameName + " for the " + console + " as soon as possible.\n"
				+ gameName + " for the " + console + " was due on " + dueDate + ".";
		

		try {
			// Create MimeMessage object & set values
			Message messageobj = new MimeMessage(sessionobj);
			messageobj.setFrom(new InternetAddress(senderEmail));
			messageobj.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinationEmail));
			messageobj.setSubject(subject);
			messageobj.setText(body);
			
			// Now send the message
			Transport.send(messageobj);
			
			emailSent.increment(1);
			
			// Logging
			log.info("Email successfully sent");
		} catch (MessagingException exp) {
			log.error("Email sending error", exp);
			throw new RuntimeException(exp);
		}
	}
	
	public Rental toggleOverdue(int gameId) {
		Rental r = null;
		
		try {
			r = rentalDAO.findById(gameId).orElse(null);
			successfulConnectionAttempts.increment(1);
		} catch (DataAccessException e) {
			failedConnectionAttempts.increment(1);
		}
		
		if(r != null) {
			r.setOverDue(!r.isOverDue());
			
			try {
				rentalDAO.save(r);
				successfulConnectionAttempts.increment(1);
			} catch (DataAccessException e) {
				failedConnectionAttempts.increment(1);
			}
			
			if(r.isOverDue()) {
				MDC.put("overdue", Integer.toString(gameId));
				this.sendEmail(r);
			}
			
			log.info("Rental updated");
		} else {
			log.error("Rental doesn't exist");
		}
		
		MDC.clear();
		return r;
	}
	
	public Rental changeDate(int gameId, LocalDate date) {
		Rental r = null;
		
		try {
			r = rentalDAO.findById(gameId).orElse(null);
			successfulConnectionAttempts.increment(1);
		} catch (DataAccessException e) {
			failedConnectionAttempts.increment(1);
		}		
			
		if(r != null) {
			r.setDueDate(date);
			
			try {
				rentalDAO.save(r);
				successfulConnectionAttempts.increment(1);
			} catch (DataAccessException e) {
				failedConnectionAttempts.increment(1);
			}
			
			log.info("Rental updated");
		} else {
			log.error("Rental doesn't exist");
		}
		
		MDC.clear();
		return r;
	}
}