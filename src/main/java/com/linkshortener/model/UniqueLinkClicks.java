package com.linkshortener.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UniqueLinkClicks {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long uniqueId;
	private String sha;
	@ManyToOne(cascade=CascadeType.REFRESH, fetch=FetchType.EAGER)
	private Link link;
}
