package org.apache.beam.examples;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee implements Serializable {
	
	private String fname;
	private String sname;
	private String email;
	private int age;
	private String phone;
	private String gender;

}
