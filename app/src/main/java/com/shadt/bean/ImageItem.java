package com.shadt.bean;

import java.io.Serializable;

/**
 * 每张图片对象
 * 
 * @author Administrator
 * 
 */
public class ImageItem implements Serializable {
	public String imageId;
	public String thumbnailPath;
	public String imagePath;
	public boolean isSelected = false;

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
}
