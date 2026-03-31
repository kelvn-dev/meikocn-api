package com.meikocn.api;

import static org.awaitility.Awaitility.await;

import java.util.concurrent.TimeUnit;

public class TempTest {
  public void test() {
    await().atMost(5, TimeUnit.SECONDS).until(() -> conditionIsTrue());
  }
}
