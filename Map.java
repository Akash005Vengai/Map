package map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Map<K,V> implements Iterable<K>,Serializable,Cloneable {
	
	private static final long serialVersionUID = -7812949453374502201L;

	public Map() {
		
	}
	
	public Map(K[] keys,V[] values) {
		if(keys.length!=values.length) {
			throw new IllegalStateException("Keys length and values length ar not same");
		}
		for(int i=0;i<keys.length;i++){
			put(keys[i],values[i]);
		}
	}

	public Map(List<K> keys,List<V> values) {
		if(keys.size()!=values.size()) {
			throw new IllegalStateException("Keys length and values length ar not same");
		}
		for(int i=0;i<keys.size();i++){
			put(keys.get(i),values.get(i));
		}
	}
	
	public Map(Map<K,V> map) {
		putAll(map);
	}
	
	@Override
	protected Map<K,V> clone()throws CloneNotSupportedException{
		return new Map<>(this);
		
	}
	
	public void putAll(Map<K,V> map) {
		map.forEach((n,m)->put(n, m));
	}
	
	public void forEach(BiConsumer<K, V> syntex) {
		if(head==null)
			return;
		if(syntex==null)
			throw new NullPointerException();
		Entry g=head;
		for(int i=0;i<length;i++) {
			syntex.accept(g.getKey(), g.getValue());
			g=g.next();
		}
	}
	
	public Map<K,V> filter(BiPredicate<K, V> syntex) {
		Map<K,V> map = new Map<>();
		forEach((n,m)->{
			if(syntex.test(n, m)) {
				map.put(n, m);
			}
		});
		return map;
	}
	
	public Map<K,V> filter(Predicate<K> syntex) {
		Map<K,V> map = new Map<>();
		forEach((n,m)->{
			if(syntex.test(n)) {
				map.put(n, m);
			}
		});
		return map;
	}
	
	public boolean containsKey(K key) {
		return getNode(key)!=null;
	}
	
	public boolean containsValue(V value){
		return getValue(value)!=null;
	}
	
	public boolean conatainsKey(K key,V value) {
		return getNode(key, value)!=null;
	}
	
	public boolean isEmpty() {
		return head==null;
	}
	
	public V remove(K key){
		Entry curr = getNode(key);
		if(curr!=null) {
			V key1 = curr.getValue();
			 if(curr.previous()==null){
					head = head.next();
			}else if(curr.next()==null){
				last = curr.previous();
				last.setNext(null);
			}else {
				curr.previous().setNext(curr.next());
				curr.next().setPrevious(curr.previous());
			}
			length--;
			return key1;
		}
		return null;
	}

	public void clear() {
		head = null;
		last = null;
	}
	
	public V put(K key,V value){
		if(key==null||value==null)
			throw new NullPointerException();
		Entry curr = getNode(key);
		if(curr!=null) {
			curr.setValue(value);
			return value;
		}
		if(length==0) {
			head = new Entry(key,value,null,null);
			last = head;
		}else if(length==1) {
			head.setNext(new Entry(key,value,null,head));
			last = head.next();
		}else {
			last.setNext(new Entry(key,value,null,last));
			last = last.next();
		}
			length++;
		return value;
	}
	
	private Entry head;
	private Entry last;
	private int length;
	
	public V get(K key){
		Entry curr = getNode(key);
		return curr==null?null:curr.getValue();
	}
	
	
	public V get(K key,V value){
		Entry curr = getNode(key,value);
		return curr==null?null:curr.getValue();
	}
	
	public int size() {
		return length;
	}
	
	@Override
	public Iterator<K> iterator() {
		return new Iterator<K>() {

			Entry curr = head;
			int size = length;
			
			@Override
			public boolean hasNext() {
				if(size!=length)
					throw new ConcurrentModificationException();
				return curr != null;
			}

			@Override
			public K next() {
				if(size!=length)
					throw new ConcurrentModificationException();
				K key = curr.getKey();
				curr = curr.next();
				return key;
			}
		};
	}
	
	
	
	public Set<K> keys(){
		Set<K> set = new HashSet<>();
		forEach((n,m)->{
			set.add(n);
		});
		return set;
	}
	
	@SuppressWarnings("unchecked")
	public V[] values() {
		ArrayList<V> values = new ArrayList<>();
		forEach((n,m)->{
			values.add(m);
		});
		return (V[]) values.toArray();
	}
	
	class Entry{
		
		private Entry previous;
		private Entry next;
		private K key;
		private V value;
		
		Entry(K key,V value,Entry next,Entry previous){
			this.key = key;
			this.value = value;
			this.next = next;
			this.previous = previous;
		}
		
		@Override
		public String toString() {
			return key+"="+value;
		}

		public Entry previous() {
			return previous;
		}

		public void setPrevious(Entry previous) {
			this.previous = previous;
		}

		public Entry next() {
			return next;
		}

		public void setNext(Entry next){
			this.next = next;
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}
		
		public void setValue(V value) {
			this.value = value;
		}

		@Override
		public int hashCode() {
			return Objects.hash(key, value);
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Entry other = (Entry) obj;
			return Objects.equals(key, other.key) && Objects.equals(value, other.value);
		}
		
	}
	
	@Override
	public String toString() {
		if(head==null) {
			return "{}";
		}
		StringBuffer buff = new StringBuffer("{");
		Entry curr = head;
		buff.append(curr.toString());
		while(curr.next()!=null) {
			curr = curr.next();
			buff.append(", "+curr.toString());
		}
		buff.append("}");
		return buff.toString();
	}
	
	private Entry getNode(K key){
		Entry curr = head;
		while(curr!=null) {
			if(curr.getKey().equals(key)) {
				return curr;
			}
			curr = curr.next();
		}
		return null;
	}
	
	private Entry getValue(V value){
		Entry curr = head;
		while(curr!=null) {
			if(curr.getValue().equals(value)) {
				return curr;
			}
			curr = curr.next();
		}
		return null;
	}

	private Entry getNode(K key,V value){
		Entry curr = head;
		while(curr!=null) {
			if(curr.getKey().equals(key)&&curr.getValue().equals(value)) {
				return curr;
			}
			curr = curr.next();
		}
		return null;
	}
	
	public K firstKey() {
		return head==null?null:head.getKey();
	}
	
	public K lastKey() {
		return last==null?null:last.getKey();
	}
	
	public V firstValue() {
		return head==null?null:head.getValue();
	}
	
	public V lastValue() {
		return last==null?null:last.getValue();
	}
	
}

/*
 * 
 * replace
 * putifAbsent
 * 
 */
