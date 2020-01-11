/*
 * Copyright (c) 2011-2019 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
 */
package com.terracottatech.tcson.writing;

public interface SonWriter<E> {

  byte DATE_SIGNIFIER = (byte) -1;
  byte UUID_SIGNIFIER = (byte) -2;

  E end();

}
