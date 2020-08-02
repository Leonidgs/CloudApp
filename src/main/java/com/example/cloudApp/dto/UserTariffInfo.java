package com.example.cloudApp.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UserTariffInfo {
	List<OfferDto> offers;
	Float usedSize;
	Integer userid;
	String tariffName;
	
}
