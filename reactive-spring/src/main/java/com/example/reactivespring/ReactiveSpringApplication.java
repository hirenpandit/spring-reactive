package com.example.reactivespring;

import com.example.reactivespring.entity.Reservation;
import com.example.reactivespring.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class ReactiveSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactiveSpringApplication.class, args);
	}

}

@Component
@RequiredArgsConstructor
class DataInitializer {
	private final ReservationRepository reservationRepository;

	@EventListener(ApplicationReadyEvent.class)
	public void ready() {
		var saved = Flux.just("Glenn", "Simon", "Josh", "Hiren", "Madhura", "Bob", "Violetta")
				.map(name -> new Reservation(null, name))
				.flatMap(r -> this.reservationRepository.save(r));

		saved.subscribe();

		this.reservationRepository
				.deleteAll()
				.thenMany(saved)
				.thenMany(this.reservationRepository.findAll())
				.subscribe(System.out::println);
	}
}
