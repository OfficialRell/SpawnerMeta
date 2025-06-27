package mc.rellox.spawnermeta.text.content;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mc.rellox.spawnermeta.text.content.Colorer.Colors;
import mc.rellox.spawnermeta.text.content.Content.Variable;

public final class ContentParser {
	
	protected static final Pattern colors, variables;
	static {
		colors = Pattern.compile("<(?:(?:#[a-f\\d]{6}(?:-#[a-f\\d]{6})*)|(?:![a-z]+))>",
				Pattern.CASE_INSENSITIVE);
		variables = Pattern.compile("%[a-z_]+%", Pattern.CASE_INSENSITIVE);
	}
	
	public static Content parse(String text) {
		return text == null || text.isEmpty() == true
				? Content.empty() : new ContentParser(text).parse();
	}
	
	public static List<Content> parse(List<String> list) {
		return list == null || list.isEmpty()
				? List.of() : list.stream()
				.map(ContentParser::parse)
				.collect(Collectors.toList());
	}
	
	private final String text;
	
	private ContentParser(String text) {
		this.text = text;
	}
	
	public Content parse() {
		if(text == null || text.isEmpty() == true) return Content.empty();
		List<Text> list = text();
		List<Content> result = new ArrayList<>();
		ContentBuilder builder = new ContentBuilder();
		for(Text t : list) {
			if(t.type == Type.text) {
				Content input = variabled(t.text);
				result.add(builder.build(input));
				builder.reset();
			} else if(t.type == Type.color) builder.color(t.text);
			else if(t.type == Type.gradient) builder.gradient(t.text);
			else builder.format(t.text);
		}
		return result.isEmpty() == true ? Content.empty()
				: result.size() == 1 ? result.get(0) : Content.of(result);
	}
	
	private List<Text> text() {
		List<Text> list = new ArrayList<>();
		Matcher m = colors.matcher(text);
		int e = 0, s;
		while(m.find() == true) {
			s = m.start();
			if(s > e) list.add(new Text(text.substring(e, s), Type.text));
			e = m.end();
			String t = m.group(), g = t.substring(1, t.length() - 1);
			if(g.indexOf('-') >= 0) list.add(new Text(g, Type.gradient));
			else if(g.indexOf('!') == 0) list.add(new Text(g, Type.format));
			else if(g.indexOf('#') == 0) list.add(new Text(g, Type.color));
			else list.add(new Text(t, Type.text));
		}
		if(e < text.length()) list.add(new Text(text.substring(e), Type.text)); 
		return list;
	}
	
	private Content variabled(String t) {
		if(t.isEmpty() == true) return Content.empty();
		List<Content> list = new ArrayList<>();
		Matcher m = variables.matcher(t);
		int e = 0, s;
		while(m.find() == true) {
			s = m.start();
			if(s > e) list.add(Content.of(t.substring(e, s)));
			e = m.end();
			String g = m.group();
			list.add(Content.of(Variable.of(g.substring(1, g.length() - 1))));
		}
		if(e < t.length()) list.add(Content.of(t.substring(e))); 
		return list.size() == 1 ? list.get(0) : Content.of(list);
	}
	
	private class ContentBuilder {
		private int[] is;
		private final List<Format> fs = new ArrayList<>();
		private void reset() {
			is = null;
			fs.clear();
		}
		private void color(String s) {
			try {
				is = new int[] {Integer.parseInt(s.substring(1), 16)};
			} catch (Exception e) {}
		}
		private void gradient(String s) {
			try {
				is = Stream.of(s.split("-"))
						.mapToInt(t -> Integer.parseInt(t.substring(1), 16))
						.toArray();
			} catch (Exception e) {}
		}
		private void format(String s) {
			Format f = switch(s.substring(1)) {
			case "bold", "b" -> Format.bold;
			case "italic", "i" -> Format.italic;
			case "obfuscated", "o" -> Format.obfuscated;
			case "strikethrough", "s" -> Format.strikethrough;
			case "underline", "u" -> Format.underline;
			default -> null;
			};
			if(f != null) fs.add(f);
		}
		private Content build(Content input) {
			Colorer color;
			if(is != null) {
				if(is.length == 1) {
					if(fs.isEmpty() == true) color = Colorer.of(is[0]);
					else color = Colorer.of(is[0], Format.of(fs));
				} else {
					if(fs.isEmpty() == true) color = Colorer.of(Colors.of(is));
					else color = Colorer.of(Colors.of(is), Format.of(fs));
				}
			} else if(fs.isEmpty() == false) color = Colorer.of(Colors.white, Format.of(fs));
			else color = null;
			return color == null ? input : Content.of(color, input);
		}
	}
	
	private record Text(String text, Type type) {}
	
	private enum Type {
		text, color, gradient, format;
	}

}
