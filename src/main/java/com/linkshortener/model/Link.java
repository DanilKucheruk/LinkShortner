package com.linkshortener.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "links")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Link {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long linkId;
	private String linkRedirect;
	private String userSha;
	private String generatedValue;
	private Long countClick = 0L;
	private Double time;
	private LocalDateTime createDate = LocalDateTime.now();
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "link")
	private List<UniqueLinkClicks> uniques;
}
