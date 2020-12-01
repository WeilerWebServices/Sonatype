package com.sonatype.workout.db;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.sonatype.example.workout.api.ExerciseService;
import com.sonatype.example.workout.api.UserService;
import com.sonatype.workout.model.Exercise;
import com.sonatype.workout.model.User;

public class ExerciseServiceDBImpl implements ExerciseService {

	private HibernateTemplate hibernateTemplate;

	public ExerciseServiceDBImpl() {
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

	public Exercise load(Integer id) {
		return (Exercise) hibernateTemplate.load(Exercise.class, id);
	}

	@Transactional(readOnly=false)
	public Integer save(Exercise exercise) {
		return (Integer) hibernateTemplate.save( exercise );
	}

	@SuppressWarnings("rawtypes")
	public List<Exercise> all() {
		List exercises = hibernateTemplate.loadAll(Exercise.class );
		return new ArrayList<Exercise>( exercises );
	}
}
