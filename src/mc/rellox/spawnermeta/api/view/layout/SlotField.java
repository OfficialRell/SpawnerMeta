package mc.rellox.spawnermeta.api.view.layout;

public enum SlotField {

	upgrade_background("background"),
	upgrade_stats("stats"),
	upgrade_range("range", true),
	upgrade_delay("delay", true),
	upgrade_amount("amount", true),
	upgrade_charges("charges");
	
	private final String defines;
	public final boolean deny;
	
	private SlotField(String defines, boolean deny) {
		this.defines = defines;
		this.deny = deny;
	}
	
	private SlotField(String defines) {
		this(defines, false);
	}
	
	public String defines() {
		return defines;
	}
	
	public boolean background() {
		return defines.equals("background");
	}

}
