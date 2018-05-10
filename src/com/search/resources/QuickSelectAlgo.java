package com.search.resources;

//We have used Quick Select Algorithm to get the
// kth largest element from the array

public class QuickSelectAlgo {
	 public int findKthLargest(int[] nums, int k) {
	        int start1 = 0, ends1 = nums.length - 1, index = nums.length - k;
	        while (start1 < ends1) {
	            int pivot = part(nums, start1, ends1);
	            if (pivot < index) start1 = pivot + 1; 
	            else if (pivot > index) ends1 = pivot - 1;
	            else return nums[pivot];
	        }
	        return nums[start1];
	    }
	    
	    private int part(int[] nums, int start1, int ends) {
	        int pivot = start1, tempp;
	        while (start1 <= ends) {
	            while (start1 <= ends && nums[start1] <= nums[pivot]) start1++;
	            while (start1 <= ends && nums[ends] > nums[pivot]) ends--;
	            if (start1 > ends) break;
	            tempp = nums[start1];
	            nums[start1] = nums[ends];
	            nums[ends] = tempp;
	        }
	        tempp = nums[ends];
	        nums[ends] = nums[pivot];
	        nums[pivot] = tempp;
	        return ends;
	    }
	    
	  }
