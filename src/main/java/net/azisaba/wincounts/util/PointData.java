package net.azisaba.wincounts.util;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PointData {

  private UUID uuid;
  private String mcid;
  private int point;
}
