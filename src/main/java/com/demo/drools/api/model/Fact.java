package com.demo.drools.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class Fact {

  @JsonProperty("inputData")
  @NotNull
  private String inputData;

  @JsonProperty("answer")
  @Size(max = 6144)
  private String answer;

  @JsonProperty("answerType")
  private GegevensType answerType;
}
