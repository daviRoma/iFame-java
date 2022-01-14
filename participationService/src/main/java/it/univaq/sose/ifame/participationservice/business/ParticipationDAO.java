package it.univaq.sose.ifame.participationservice.business;

import java.util.List;

import it.univaq.sose.ifame.participationservice.model.Participation;

public interface ParticipationDAO {

	Participation find(Integer eventId);
	List<Integer> find(String username);
	List<Participation> find (List<Integer> eventIds);
	void insert(Participation participation);
	void update(String newUsername, String oldUsername);
	void delete(Participation participation);
}
