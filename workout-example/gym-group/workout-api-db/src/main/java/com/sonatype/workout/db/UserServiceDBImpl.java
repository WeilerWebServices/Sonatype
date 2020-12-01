package com.sonatype.workout.db;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.sonatype.example.workout.api.UserService;
import com.sonatype.workout.model.User;

public class UserServiceDBImpl implements UserService {

	private HibernateTemplate hibernateTemplate;

	public UserServiceDBImpl() {
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

	public User load(Integer id) {
		return (User) hibernateTemplate.load(User.class, id);
	}

	public User forLogin(String login) {
		List<User> users = new ArrayList<User>( hibernateTemplate.findByCriteria(DetachedCriteria.forClass(User.class)
				.add(Property.forName("login").eq(login))) );
		if( users != null && users.size() > 0 ) {
			return users.get(0);
		} else {
			// TODO:Never return null this is dumb	
			return null;
		}
	}

	@Transactional(readOnly=false)
	public Integer save(User user) {
		return (Integer) hibernateTemplate.save( user );
	}
}
