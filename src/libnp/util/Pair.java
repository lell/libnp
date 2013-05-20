package libnp.util;

public class Pair<Left, Right> {
	public final Left left;
	public final Right right;
	public Pair(Left left, Right right) {
		this.left = left;
		this.right = right;
	}
	
	public int hashCode() {
		int hashLeft = left != null ? left.hashCode() : 0;
		int hashRight = right != null ? right.hashCode() : 0;
		
		return (hashLeft + hashRight) * hashRight + hashLeft;
	}
	
	public boolean equals(Object other) {
		if (!(other instanceof Pair)) {
			return false;
		}
		Pair<Left, Right> otherPair = (Pair) other;
		
		if (this.left == otherPair.left && this.right == otherPair.right) {
			return true;
		} else if (otherPair.left == null && this.left != null) {
			return false;
		} else if (otherPair.right == null && this.right != null) {
			return false;
		} else if (this.left == null && otherPair.left != null) {
			return false;
		} else if (this.right == null && otherPair.right != null) {
			return false;
		} else if (this.left != null && !this.left.equals(otherPair.left)) {
			return false;
		} else if (this.right != null && !this.right.equals(otherPair.right)) {
			return false;
		}
		return true;
	}
}
