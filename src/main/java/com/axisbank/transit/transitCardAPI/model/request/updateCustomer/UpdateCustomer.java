package com.axisbank.transit.transitCardAPI.model.request.updateCustomer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateCustomer {

   @JsonProperty("Customer")
    private Customer customer;

   public Customer getCustomer() {
      return customer;
   }

   public void setCustomer(Customer customer) {
      this.customer = customer;
   }

   @Override
   public String toString() {
      return "UpdateCustomer{" +
              "customer=" + customer +
              '}';
   }
}

