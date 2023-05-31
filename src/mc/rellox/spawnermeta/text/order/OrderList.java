package mc.rellox.spawnermeta.text.order;

import java.util.List;

import mc.rellox.spawnermeta.text.order.IOrder.Orderer;

public record OrderList(List<String> keys) {
	
	public OrderList {
		keys.removeIf(e -> e.length() > 1 && e.charAt(e.length() - 1) == '!');
	}
	
	public IOrder oderer() {
		return new Orderer(this);
	}

}
