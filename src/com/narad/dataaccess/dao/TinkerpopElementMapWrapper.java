package com.narad.dataaccess.dao;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.tinkerpop.blueprints.Element;

class TinkerpopElementMapWrapper implements Map<String, Object> {
	/**
	 * 
	 */
	private Element element;

	public TinkerpopElementMapWrapper(Element element) {
		super();
		this.element = element;
	}

	@Override
	public int size() {
		return element.getPropertyKeys().size();
	}

	@Override
	public boolean isEmpty() {
		return size() == 0 ? true : false;
	}

	@Override
	public boolean containsKey(Object paramObject) {
		return element.getPropertyKeys().contains(paramObject);
	}

	@Override
	public boolean containsValue(Object paramObject) {
		return false;
	}

	@Override
	public Object get(Object paramObject) {
		return element.getProperty((String) paramObject);
	}

	@Override
	public Object put(String paramK, Object paramV) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object remove(Object paramObject) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map paramMap) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set keySet() {
		return element.getPropertyKeys();
	}

	@Override
	public Collection values() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set entrySet() {
		throw new UnsupportedOperationException();
	}
}