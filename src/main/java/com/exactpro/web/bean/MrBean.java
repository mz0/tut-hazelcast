package com.exactpro.web.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@ManagedBean(name = "mrBean")
@ViewScoped
public class MrBean implements Serializable {
    private Map<String, String> proto2plugin = new HashMap<String, String>() {{
		put("bb", "Blagin");
		put("kk", "Klagin");
	}};

    public Map<String, String> getMap() { return proto2plugin; }
    public void setMap(Map<String, String> proto2className) { this.proto2plugin = proto2className; }
}
