package trashbox.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FirstController {
	@Autowired
	private ApplicationContext ac;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public @ResponseBody ModelAndView defaultPage() {
		return  new ModelAndView("index");
	}
	
	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public @ResponseBody String provideUploadInfo() {
		return "You can upload a file by posting to this same URL.";
	}

	
	@RequestMapping(value = "/create_table", method = RequestMethod.GET)
	public @ResponseBody String createDB() {
		DataSource dataSource = (DataSource) ac.getBean("dataSource");

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			jdbcTemplate
			.update("CREATE TABLE customer (id MEDIUMINT NOT NULL AUTO_INCREMENT, first_name CHAR(100), PRIMARY KEY (id))");
			return "Table created!";
		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
		
	}

	@RequestMapping(value = "/add_customer", method = RequestMethod.GET)
	public @ResponseBody String addCustomer() {
		DataSource dataSource = (DataSource) ac.getBean("dataSource");

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update("INSERT INTO customer(first_name) VALUES('"
				+ Long.toString(System.currentTimeMillis()) + "')");
		return "done.";
	}

	@RequestMapping(value = "/get_customers", method = RequestMethod.GET)
	public @ResponseBody String provideCustomerNames() {
		DataSource dataSource = (DataSource) ac.getBean("dataSource");

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		SqlRowSet resultSet = jdbcTemplate
				.queryForRowSet("SELECT * FROM customer");
		String returnStr = "";
		while (resultSet.next()) {
			returnStr += resultSet.getString("first_name") + " ";
		}
		return returnStr;
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody String handleFileUpload(
			@RequestParam("name") String name,
			@RequestParam("file") MultipartFile file) {
		if (!file.isEmpty()) {
			try {
				byte[] bytes = file.getBytes();
				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(new File(name + "-uploaded")));
				stream.write(bytes);
				stream.close();
				return "You successfully uploaded " + name + " into " + name
						+ "-uploaded !";
			} catch (Exception e) {
				return "You failed to upload " + name + " => " + e.getMessage();
			}
		} else {
			return "You failed to upload " + name
					+ " because the file was empty.";
		}
	}

}