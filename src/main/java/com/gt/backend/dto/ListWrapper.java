package com.gt.backend.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@XmlType
@Getter
@Setter
@NoArgsConstructor
public class ListWrapper {
	
	@XmlElement
	@EqualsAndHashCode.Exclude
	private List<String> list;
}
