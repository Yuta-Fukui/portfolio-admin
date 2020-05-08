package com.seattleacademy.team20;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

@Controller
public class SkillController {

	private static final Logger logger = LoggerFactory.getLogger(SkillController.class);

	@RequestMapping(value="/skillUpload", method = RequestMethod.GET)
	public String upload(Locale locale, Model model) throws IOException {
		logger.info("home! The client locale is {}.", locale);

		initialize();

		List<Skill> skills = selectSkills();

		updateSKill(skills);

		return "skillUpload";
	}
	//タスク10
	@Autowired
//　mySQLに接続する（データを取得できるようにする）
	private JdbcTemplate jdbcTemplate;

	public List<Skill> selectSkills() {
		final String sql = "select * from skills";
		return jdbcTemplate.query(sql, new RowMapper<Skill>() {
			public Skill mapRow(ResultSet rs,int RowNum) throws SQLException {
				return new Skill(rs.getString("category"),rs.getString("name"),rs.getInt("score"));
			}
		});
	}

	private FirebaseApp app;

//	初期化する
	public void initialize() throws IOException {
		FileInputStream refreshToken = new FileInputStream("/Users/yuta/Downloads/develop-portfolio-firebase-adminsdk-b1d8dd560f05c8.json");
		FirebaseOptions options = new FirebaseOptions.Builder()
			    .setCredentials(GoogleCredentials.fromStream(refreshToken))
			    .setDatabaseUrl("https://develop-portfolio.firebaseio.com/")
			    .build();

		app = FirebaseApp.initializeApp(options, "other");
	}

	public void updateSKill(List<Skill> skills) {
		final FirebaseDatabase database = FirebaseDatabase.getInstance(app);
		DatabaseReference ref = database.getReference("skillCategories");
//		データの取得、形成
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
						Map<String, Object> dataMap;
						Map<String, List<Skill>> skillMap = skills.stream().collect(Collectors.groupingBy(Skill::getCategory));
						for (Map.Entry<String,List<Skill>>entry : skillMap.entrySet()){
						dataMap = new HashMap<>();
						dataMap.put("category" , entry.getKey());
						dataMap.put("skill", entry.getValue());
						dataList.add(dataMap);



//		データを送る
		ref.setValue(dataList, new DatabaseReference.CompletionListener() {
		@Override
		public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
			if (databaseError != null) {
				System.out.println("Data could not be served" + databaseError.getMessage());
			} else {
				System.out.println("Data  served successfully.");
			}
		 }
	  });
	}
  }
}
