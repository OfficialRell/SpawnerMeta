package mc.rellox.spawnermeta.text.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import mc.rellox.spawnermeta.text.Text;
import mc.rellox.spawnermeta.text.content.Content;

public interface IOrder {
	
	void submit(String type, Supplier<List<Content>> supplier);
	
	List<String> build();
	
	void named(List<Content> overflow);
	
	class Orderer implements IOrder {
		
		private final OrderList list;
		
		private final Map<String, Supplier<List<Content>>> map;
		
		public Orderer(OrderList list) {
			this.list = list;
			this.map = new HashMap<>();
		}

		@Override
		public void submit(String key, Supplier<List<Content>> supplier) {
			map.put(key, supplier);
		}
		
		@Override
		public void named(List<Content> name) {
			if(name.isEmpty() == true) return;
			map.put("NAMED", () -> name);
		}

		@Override
		public List<String> build() {
			List<String> build = new ArrayList<>();
			list.keys().forEach(key -> {
				if(key.equals("!") == true) build.add("");
				else {
					Supplier<List<Content>> supplier = map.get(key);
					if(supplier == null) return;
					List<Content> contents = supplier.get();
					if(contents == null || contents.isEmpty() == true) return;
					List<String> text = Text.toText(contents);
					if(text.isEmpty() == true) return;
					build.addAll(text);
				}
			});
			Text.clean(build);
			return build;
		}
		
	}

}
