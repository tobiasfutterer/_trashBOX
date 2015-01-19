package trashbox.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import trashbox.domain.UserAccount;

@Controller
public class FirstController {
	@Autowired
	private ApplicationContext ac;
	
	@Autowired
	private UserAccount userAcc;
	
	@Value( "${hello.value:World}" )
    private String helloValue;
	
	@RequestMapping("/")
	public String userProfile(Map<String, Object> model) {
		model.put("time", new Date());
		model.put("message","yo!");
		return "userProfile";
	}

	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public @ResponseBody String sayHello()
    {
		try {
			return "Hello " + helloValue;
		} catch (Exception e) {
			// TODO: handle exception
			return e.getMessage();
		}        
    }
	
	@RequestMapping(value = "/whoami", method = RequestMethod.GET)
	public @ResponseBody String whoAmI(HttpSession session)
    {
		try {
			return "Hello " + userAcc.getFirstName() + " " + userAcc.getLastName() + " finally " + session.getAttribute("firstlast");
		} catch (Exception e) {
			// TODO: handle exception
			return e.getMessage();
		}        
    }
	@RequestMapping(value = "/iam", method = RequestMethod.GET)
	public ModelAndView iAm(@RequestParam("first") String first,@RequestParam("last") String last, HttpSession session) throws Exception
    {
        session.setAttribute("firstlast", (String)first+last);
        
		if(userAcc != null){
		userAcc.setFirstName(first);
		userAcc.setLastName(last);
		}
		else
		{
			userAcc = new UserAccount(1,first,last);
		}
		ModelAndView model = new ModelAndView("userProfile");
		 
		return model;
    }
	
	@RequestMapping("/test")
	public String userProfile(ModelAndView modelAndView)
	{
	    return "userProfile";
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
		jdbcTemplate.update("INSERT INTO customer(first_name) VALUES(?)",Long.toString(System.currentTimeMillis()));
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

}