package com.sonatype.workout.db;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.sonatype.example.workout.api.WorkoutService;
import com.sonatype.workout.model.Workout;

public class WorkoutServiceDBImpl implements WorkoutService {

	private HibernateTemplate hibernateTemplate;

	public WorkoutServiceDBImpl() {
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

	public Workout load(Integer id) {
		return (Workout) hibernateTemplate.load(Workout.class, id);
	}

	@Transactional(readOnly=false)
	public Integer save(Workout workout) {
		return (Integer) hibernateTemplate.save( workout );
	}
}
