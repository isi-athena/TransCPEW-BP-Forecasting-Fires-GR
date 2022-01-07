package gr.isi.iolaos;

import javax.mail.PasswordAuthentication;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;

import javax.mail.*;

import org.apache.commons.io.IOUtils;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.interceptor.Session;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.FileValue;
import org.camunda.bpm.engine.variable.value.StringValue;

import camundajar.impl.scala.util.Properties;

public class JavaMailDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// TODO Auto-generated method stub

		
		//add smtp propeties
		java.util.Properties prop=new java.util.Properties();
		prop.put("mail.smtp.auth", true);
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", "smtp-relay.sendinblue.com");
		prop.put("mail.smtp.port", "587");

		javax.mail.Session session = javax.mail.Session.getInstance(prop, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("chranagno@gmail.com", "pgcEak3CdQVDbhm1");
			}
		});

		ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
		RuntimeService runtimeService=processEngine.getRuntimeService();



		//Create message
		Message message=new MimeMessage(session);
		
		//Sent from address
		message.setFrom(new InternetAddress("chranagno@yahoo.gr"));

		//Get recipients//
		//java.util.ArrayList<String> recipients=(java.util.ArrayList<String>)execution.getVariable("To");
		//String groupID=EmailRecipientsGroupIDs.GR_ESKE_ORGS;//(String)execution.getVariable("grouID");
		String groupID=(String)execution.getVariable("Recipients");
		message.setRecipients(Message.RecipientType.BCC, getRecipientsFromGroup(processEngine, groupID));
		//		message.setRecipients(
		//				Message.RecipientType.TO, InternetAddress.parse("chranagno@gmail.com"));
		
		
		//Get subject
		String subject=(String)execution.getVariable("Subject");
		message.setSubject(subject);
		
		//Get Body
		String msg=(String)execution.getVariable("Body");
		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(msg, "text/html");
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(mimeBodyPart);
		
		//Get map
		FileValue retrievedTypedFileValue=runtimeService.getVariableTyped(execution.getId(),"map");
		InputStream mapContent=retrievedTypedFileValue.getValue();

		File new_file = new File("test.jpg");

		String mes="";
		try(OutputStream outputStream = new FileOutputStream(new_file)){
			IOUtils.copy(mapContent, outputStream);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			mes=e.getMessage();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			mes=e.getMessage();
		}

		StringValue typedStringValue = Variables.stringValue(mes);
		runtimeService.setVariable(execution.getId(), "message", typedStringValue);




		//Files.createTempFile(msg, msg, null)
		MimeBodyPart attachmentBodyPart = new MimeBodyPart();
		attachmentBodyPart.attachFile(new_file);
		multipart.addBodyPart(attachmentBodyPart);
		message.setContent(multipart);

		//Send message
		if(execution.getVariable("SendMessage")=="True")
		{
			Transport.send(message);
		}

		new_file.delete();


	}

	private Address[] getRecipientsFromList(java.util.ArrayList<String> emails) throws AddressException {
		Address[] addresses = new Address[emails.size()];
		for (int i =0;i < emails.size();i++) {
			addresses[i] = new InternetAddress(emails.get(i));
		}
		return addresses;
	}

	private Address[] getRecipientsFromGroup(ProcessEngine engine, String userID) throws AddressException
	{
		List<User> l=engine.getIdentityService().createUserQuery().memberOfGroup(userID).list();
		Address[] addresses = new Address[l.size()];
		for(int i=0;i<l.size();i++)
		{
			addresses[i] = new InternetAddress( l.get(i).getEmail());
		}
		return addresses;
	}
	
	private String getName(ProcessEngine engine,RuntimeService service)
	{
		return null;
	}



}


