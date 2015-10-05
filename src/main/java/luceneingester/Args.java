package luceneingester;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Args {
  private final String[] args;
  private final Map<String,Boolean> used = new HashMap<String,Boolean>();

  public Args(String[] args) {
    this.args = args;
  }

  public String getString(String argName) {
    for(int upto=0;upto<args.length;upto++) {
      if (args[upto].equals(argName)) {
        if (upto == args.length-1) {
          throw new RuntimeException("missing value for argument " + argName);
        }
        used.put(argName, true);
        return args[1+upto];
      }
    }

    throw new RuntimeException("missing required argument: " + argName);
  }

  public List<String> getStrings(String argName) {
    List<String> values = new ArrayList<String>();
    for(int upto=0;upto<args.length;upto++) {
      if (args[upto].equals(argName)) {
        if (upto == args.length-1) {
          throw new RuntimeException("missing value for argument " + argName);
        }
        used.put(argName, true);
        values.add(args[1+upto]);
      }
    }
    if (values.size() == 0) {
      throw new RuntimeException("missing required argument: " + argName);
    }
    return values;
  }

  public String getString(String argName, String defaultValue) {
    for(int upto=0;upto<args.length;upto++) {
      if (args[upto].equals(argName)) {
        if (upto == args.length-1) {
          throw new RuntimeException("missing value for argument " + argName);
        }
        used.put(argName, true);
        return args[1+upto];
      }
    }

    return defaultValue;
  }

  public int getInt(String argName) {
    return Integer.parseInt(getString(argName));
  }

  public double getDouble(String argName) {
    return Double.parseDouble(getString(argName));
  }

  public float getFloat(String argName) {
    return Float.parseFloat(getString(argName));
  }

  public long getLong(String argName) {
    return Long.parseLong(getString(argName));
  }

  public boolean getFlag(String argName) {
    for(int upto=0;upto<args.length;upto++) {
      if (args[upto].equals(argName)) {
        used.put(argName, false);
        return true;
      }
    }

    return false;
  }

  public void check() {
    for(int upto=0;upto<args.length;upto++) {
      Boolean v = used.get(args[upto]);
      if (v == null) {
        throw new RuntimeException("argument " + args[upto] + " isn't recognized");
      } else if (v) {
        upto++;
      }
    }
  }
}
