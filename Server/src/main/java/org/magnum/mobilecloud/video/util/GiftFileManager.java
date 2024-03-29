/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.magnum.mobilecloud.video.util;


import org.magnum.mobilecloud.video.core.Gift;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * This class provides a simple implementation to store gift binary
 * data on the file system in a "gifts" folder. The class provides
 * methods for saving gifts and retrieving their binary data.
 * 
 * @author jules
 *
 */
public class GiftFileManager {

	/**
	 * This static factory method creates and returns a
	 * GiftFileManager object to the caller. Feel free to customize
	 * this method to take parameters, etc. if you want.
	 *
	 * @return
	 * @throws java.io.IOException
	 */
	public static GiftFileManager get() throws IOException {
		return new GiftFileManager();
	}

	private Path targetDir_ = Paths.get("gifts");

	// The GiftFileManager.get() method should be used
	// to obtain an instance
	private GiftFileManager() throws IOException{
		if(!Files.exists(targetDir_)){
			Files.createDirectories(targetDir_);
		}
	}

	// Private helper method for resolving gift file paths
	private Path getGiftPath(Gift v){
		assert(v != null);

		return targetDir_.resolve("gift"+v.getId()+".jpg");
	}

	/**
	 * This method returns true if the specified Gift has binary
	 * data stored on the file system.
	 *
	 * @param v
	 * @return
	 */
	public boolean hasGiftData(Gift v){
		Path source = getGiftPath(v);
		return Files.exists(source);
	}

	/**
	 * This method copies the binary data for the given gift to
	 * the provided output stream. The caller is responsible for
	 * ensuring that the specified Gift has binary data associated
	 * with it. If not, this method will throw a FileNotFoundException.
	 *
	 * @param v
	 * @param out
	 * @throws java.io.IOException
	 */
	public void copyGiftData(Gift v, OutputStream out) throws IOException {
		Path source = getGiftPath(v);
		if(!Files.exists(source)){
			throw new FileNotFoundException("Unable to find the referenced gift file for giftId:"+v.getId());
		}
		Files.copy(source, out);
	}

	/**
	 * This method reads all of the data in the provided InputStream and stores
	 * it on the file system. The data is associated with the Gift object that
	 * is provided by the caller.
	 *
	 * @param v
	 * @param giftData
	 * @throws java.io.IOException
	 */
	public void saveGiftData(Gift v, InputStream giftData) throws IOException{
		assert(giftData != null);
		
		Path target = getGiftPath(v);
		Files.copy(giftData, target, StandardCopyOption.REPLACE_EXISTING);
	}
	
}
