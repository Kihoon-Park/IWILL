package com.project.service.user;

import java.util.Optional;

import javax.validation.Valid;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.project.dao.user.UserDAO;
import com.project.model.user.UserEntity;
import com.project.service.will.FileService;

@Service
public class UserService {

	@Autowired
	UserDAO userdao;

	FileService fs = new FileService();
	
	public Object login(String email, String password){
		if (email == null || password == null)
			return null;


		Optional<UserEntity> user = userdao.findByEmailAndUpw(email, password);
		

		if(user != null){
			JSONObject request = new JSONObject();
			JSONArray params = new JSONArray();
			request.put("jsonrpc", "2.0");
			request.put("method", "personal_unlockAccount");
			System.out.println(user.get().getAccounthash());
			params.put(user.get().getAccounthash());
			params.put("");
			params.put(0);
			request.put("params", params);
			request.put("id", 10);
			try{
				String result = fs.sendPost("http://localhost:8545/", request.toString());
			}
			catch(Exception e){
				System.out.println(e.getMessage());
			}
			
			

			return user;
		}
		else{
			return null;
		}

		
	}

	public Object signup(UserEntity user) throws Exception {

		UserEntity newuser = new UserEntity();
		newuser.setUpw(user.getUpw());
		newuser.setEmail(user.getEmail());
		newuser.setName(user.getName());
		newuser.setPhone(user.getPhone());
		newuser.setCreatedate(user.getCreatedate());
		newuser.setSenddate(user.getSenddate());
		newuser.setProfile(user.getProfile());
		newuser.setUsertype(user.getUsertype());

		JSONObject request = new JSONObject();
		JSONArray params = new JSONArray();
		request.put("jsonrpc", "2.0");
		request.put("method", "personal_newAccount");
		params.put("");
		request.put("params", params);
		request.put("id", 10);
		
		String result = fs.sendPost("http://localhost:8545/", request.toString());
		request = new JSONObject(result);
		
		newuser.setAccounthash(request.getString("result"));
		userdao.save(newuser);

		return new ResponseEntity<UserEntity>(newuser, HttpStatus.OK);
	}

	public Object detail(String email) {
		UserEntity user = userdao.getUserByEmail(email);

		return user;
	}

	public void update(UserEntity user) {
		Optional<UserEntity> modify = userdao.findByUid(user.getUid());

		modify.ifPresent(selectUser -> {
			selectUser.setUpw(user.getUpw());
			selectUser.setPhone(user.getPhone());
			selectUser.setProfile(user.getProfile());

			userdao.save(selectUser);
			System.out.println("update!!");
		});

	}

	public void delete(int uid) {
		Optional<UserEntity> del = userdao.findByUid(uid);

		del.ifPresent(selectUser -> {
			userdao.delete(selectUser);
			System.out.println("delete !!");
		});
	}

	public Object findpw(String email) {
		UserEntity user = userdao.getUserByEmail(email);
		
		return user;
	}

}
