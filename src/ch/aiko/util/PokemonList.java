package ch.aiko.util;

import java.util.ArrayList;

public class PokemonList<T> extends ArrayList<T> {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public T get(int index) {
		return index < 0 ? (T) (Object) 0 : super.get(index);
	}

}
