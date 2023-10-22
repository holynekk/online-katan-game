package com.group12.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Customer {
  private Long customerId;
  private String firstName;
  private String lastName;
  private String email;
}
