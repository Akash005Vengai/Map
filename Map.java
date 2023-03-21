package map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

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
		if(syntex==null)
			throw new NullPointerException();
		Node<K,V> g=head;
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
	
	public boolean containsKey(K key) {
		return getNode(key)!=null;
	}
	
	public boolean conatainsKey(K key,V value) {
		return getNode(key, value)!=null;
	}
	
	public boolean isEmpty() {
		return head==null;
	}
	
	public V remove(K key) {
		Node<K,V> curr = getNode(key);
		if(curr!=null) {
			V key1 = curr.getValue();
			if(curr.next()==null) {
				last = curr.previous();
				last.setNext(null);
			}else if(curr.previous()==null) {
				head = head.next();
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
		Node<K,V> curr = getNode(key);
		if(curr!=null) {
			curr.setValue(value);
			return value;
		}
		if(length==0) {
			head = new Node<>(key,value,null,null);
			last = head;
		}else if(length==1) {
			head.setNext(new Node<>(key,value,null,head));
			last = head.next();
		}else {
			last.setNext(new Node<>(key,value,null,last));
			last = last.next();
		}
			length++;
		return value;
	}
	
	private Node<K,V> head;
	private Node<K,V> last;
	private int length;
	
	@Override
	public String toString() {
		if(head==null) {
			return "{}";
		}
		StringBuffer buff = new StringBuffer("{");
		Node<K,V> curr = head;
		buff.append(curr.toString());
		while(curr.next()!=null) {
			curr = curr.next();
			buff.append(", "+curr.toString());
		}
		buff.append("}");
		return buff.toString();
	}
	
	private Node<K,V> getNode(K key){
		Node<K,V> curr = head;
		while(curr!=null) {
			if(curr.getKey().equals(key)) {
				return curr;
			}
			curr = curr.next();
		}
		return null;
	}

	private Node<K,V> get(int index) throws MapIndexOutOfBoundsException{
		if(index<0||length<=index)
			throw new MapIndexOutOfBoundsException("Index "+index+" out of bounds for length "+length);
		if(index == 0) {
			return head;
		}else if(length-1 == index) {
			return last;
		}else if(index>length/2){
			Node<K,V> curr = last;
			for(int i = length-2;i>length/2;i--)
				curr = curr.previous();
			return curr;
		}else {
			Node<K,V> curr = head;
			for(int i = 1;i<length/2;i++)
				curr = curr.next();
			return curr;
		}
	}
	
	private Node<K,V> getNode(K key,V value){
		Node<K,V> curr = head;
		while(curr!=null) {
			if(curr.getKey().equals(key)&&curr.getValue().equals(value)) {
				return curr;
			}
			curr = curr.next();
		}
		return null;
	}
	
	public V get(K key){
		Node<K,V> curr = getNode(key);
		return curr==null?null:curr.getValue();
	}
	
	public V getElement(int index) throws MapIndexOutOfBoundsException {
		Node<K,V> curr = get(index);
		return curr==null?null:curr.getValue();
	}
	
	public V get(K key,V value){
		Node<K,V> curr = getNode(key,value);
		return curr==null?null:curr.getValue();
	}
	
	public int size() {
		return length;
	}
	
	
	@Override
	public Iterator<K> iterator() {
		return new Iterator<K>() {

			Node<K,V> curr = head;
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
	
}

/*
 * set
 */

class Node<K,V>{
	
	private Node<K,V> previous;
	private Node<K,V> next;
	private K key;
	private V value;
	
	Node(K key,V value,Node<K,V> next,Node<K,V> previous){
		this.key = key;
		this.value = value;
		this.next = next;
		this.previous = previous;
	}
	
	@Override
	public String toString() {
		return key+"="+value;
	}

	public Node<K, V> previous() {
		return previous;
	}

	public void setPrevious(Node<K, V> previous) {
		this.previous = previous;
	}

	public Node<K, V> next() {
		return next;
	}

	public void setNext(Node<K, V> next) {
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
		Node<K,V> other = (Node<K,V>) obj;
		return Objects.equals(key, other.key) && Objects.equals(value, other.value);
	}
	
}
