package org.newcih.service.asm;

import java.util.Objects;

public class DemoCodeForAsm {

    public static DemoCodeForAsm getInstance() {
        return new DemoCodeForAsm();
    }

    public int getInt() {
        DemoCodeForAsm a = new DemoCodeForAsm();
        DemoCodeForAsm tmp = DemoCodeForAsm.getInstance();
        if (Objects.equals(a, tmp)) {
            System.out.println("two object is equals");
            return 35;
        } else {
            int c = 235;
            int d = 22;
            System.out.println(c + d);
            return c * d;
        }
    }
}
