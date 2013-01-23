package org.osehra.cpe.vpr.sync.vista;

import org.osehra.cpe.vpr.pom.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Foo extends AbstractPatientObject implements IPatientObject {

    private String bar;
    private boolean baz;

    public Foo() {
        super(null);
    }

    public Foo(Map props) {
        super(props);
    }

    public Foo(String bar, boolean baz) {
        super(null);
        setData("bar", bar);
        setData("baz", baz);
    }

    public String getBar() {
        return bar;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }

    public boolean isBaz() {
        return baz;
    }

    public void setBaz(boolean baz) {
        this.baz = baz;
    }

}
