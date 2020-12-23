package com.hzoom.core.concurrent.activeObject;

import java.util.concurrent.Future;

public interface SampleActiveObject {

    Future<String> getA();

    String getB();

}
