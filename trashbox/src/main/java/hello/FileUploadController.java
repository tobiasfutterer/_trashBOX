package hello;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileUploadController {
	private ApplicationContext ac = new FileSystemXmlApplicationContext(
			"src/main/resources/static/hsql_config.xml");

	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public @ResponseBody String provideUploadInfo() {
		return "You can upload a file by posting to this same URL.";
	}

	@RequestMapping(value = "/add_customer", method = RequestMethod.GET)
	public @ResponseBody String addCustomer() {
		DataSource dataSource = (DataSource) ac.getBean("dataSource");

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update("INSERT INTO customer(first_name) VALUES('tobi')");
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
			returnStr += resultSet.getString("first_name") + " " ;					
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