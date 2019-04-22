package io.github.benslabbert.trak.entity.jpa;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.junit.Test;

public class PriceTest {

  @Test
  public void equalsTest_true_allEqual() {

    Price p1 = Price.builder().created(new Date()).listedPrice(1L).currentPrice(1L).build();

    Price p2 = Price.builder().created(new Date()).listedPrice(1L).currentPrice(1L).build();

    boolean equals = Price.equals(p1, p2);

    assertTrue(equals);
  }

  @Test
  public void equalsTest_true_datesUnequal() {

    Price p1 =
        Price.builder()
            .created(new GregorianCalendar(2019, Calendar.MARCH, 5, 10, 10, 5).getTime())
            .listedPrice(1L)
            .currentPrice(1L)
            .build();

    Price p2 =
        Price.builder()
            .created(new GregorianCalendar(2019, Calendar.MARCH, 5, 12, 10, 10).getTime())
            .listedPrice(1L)
            .currentPrice(1L)
            .build();

    boolean equals = Price.equals(p1, p2);

    assertTrue(equals);
  }

  @Test
  public void equalsTest_false_1() {

    Price p1 =
        Price.builder()
            .created(new GregorianCalendar(2019, Calendar.MARCH, 5, 10, 10, 5).getTime())
            .listedPrice(2L)
            .currentPrice(1L)
            .build();

    Price p2 =
        Price.builder()
            .created(new GregorianCalendar(2019, Calendar.MARCH, 5, 12, 10, 10).getTime())
            .listedPrice(1L)
            .currentPrice(1L)
            .build();

    boolean equals = Price.equals(p1, p2);

    assertFalse(equals);
  }

  @Test
  public void equalsTest_false_2() {

    Price p1 =
        Price.builder()
            .created(new GregorianCalendar(2019, Calendar.MARCH, 5, 10, 10, 5).getTime())
            .listedPrice(1L)
            .currentPrice(2L)
            .build();

    Price p2 =
        Price.builder()
            .created(new GregorianCalendar(2019, Calendar.MARCH, 5, 12, 10, 10).getTime())
            .listedPrice(1L)
            .currentPrice(1L)
            .build();

    boolean equals = Price.equals(p1, p2);

    assertFalse(equals);
  }

  @Test
  public void equalsTest_false_3() {

    Price p1 =
        Price.builder()
            .created(new GregorianCalendar(2019, Calendar.MARCH, 6, 10, 10, 5).getTime())
            .listedPrice(1L)
            .currentPrice(1L)
            .build();

    Price p2 =
        Price.builder()
            .created(new GregorianCalendar(2019, Calendar.MARCH, 5, 12, 10, 10).getTime())
            .listedPrice(1L)
            .currentPrice(1L)
            .build();

    boolean equals = Price.equals(p1, p2);

    assertFalse(equals);
  }
}
