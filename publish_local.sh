#!/bin/sh

sbt ++2.10.0 \
  common/publishLocal \
  assets/publishLocal \
  orm/publishLocal \
  factoryGirl/publishLocal \
  validator/publishLocal \
  framework/publishLocal \
  mailer/publishLocal \
  standalone/publishLocal \
  task/publishLocal \
  test/publishLocal \
  freemarker/publishLocal \
  thymeleaf/publishLocal 

