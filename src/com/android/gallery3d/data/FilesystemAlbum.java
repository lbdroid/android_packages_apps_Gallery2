package com.android.gallery3d.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.util.GalleryUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class FilesystemAlbum extends MediaSet {

    private final GalleryApp mApplication;
    private final String mName;
    private final File[] mFileList;
    private final File currentPath;
    
    public FilesystemAlbum(Path path, GalleryApp application, String name){
    	super(path, nextVersionNumber());
    	mApplication = application;
    	mName = name;
    	
    	File assetfile = new File("/data/data/com.android.gallery3d/files/directory.png");
    	if (!assetfile.exists()){
            AssetManager assetManager = ((Context)mApplication).getAssets();
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open("directory.png");
                String newFileName = "/data/data/com.android.gallery3d/files/directory.png";
                out = new FileOutputStream(newFileName);

                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1)
                    out.write(buffer, 0, read);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch (Exception e) {
                Log.e("tag", e.getMessage());
            }
     	}
    	
    	Log.d("FILESYSTEMALBUM","PATH:"+path.toString());
    	String pathString = path.toString();
    	if (!pathString.startsWith("/")) pathString = "/";
    	File f = new File(pathString);
    	currentPath = f;
    	File[] files = f.listFiles();
    	ArrayList<File> filearr = new ArrayList<File>();
    	
    	// add parent directory, if not root.
    	File parentorself = f.getParentFile();
    	if (parentorself != null)
    		filearr.add(parentorself);
    	
    	// first iteration through the list, add all the directories (at the beginning).
    	for (int i=0; i<files.length; i++){
    		if (files[i].isDirectory()){
    			filearr.add(files[i]);
    			Log.d("FILESYSTEMALBUM1",files[i].getAbsolutePath());
    		}
    	}
    	
    	// second iteration through the list, add all images.
    	for (int i=0; i<files.length; i++){
    		if (!files[i].isDirectory()){
    			String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(files[i].getAbsolutePath().substring(files[i].getAbsolutePath().lastIndexOf(".")+1).toLowerCase());
    			if (mime != null && mime.contains("image")){
    				filearr.add(files[i]);
    				Log.d("FILESYSTEMALBUM2",files[i].getAbsolutePath());
    			}
    		}
    	}
    	Log.d("FILESYSTEMALBUM","Size: "+filearr.size());
    	mFileList = filearr.toArray(new File[filearr.size()]);
    }
    
    public GalleryApp getApplication(){
    	return mApplication;
    }
    
    @Override
    public boolean isCameraRoll() {
        return false;
    }

    @Override
    public Uri getContentUri() {
        return Uri.parse("file://"+mName);
    }
    
    @Override
    public ArrayList<MediaItem> getMediaItem(int start, int count) {
    	Log.d("FILESYSTEMALBUM","getMediaItem: "+start+", "+count);
    	if (start < mFileList.length){
    		ArrayList<MediaItem> list = new ArrayList<MediaItem>();
    		GalleryUtils.assertNotInRenderThread();
    		for (int i = start; i<start+count && i<mFileList.length; i++){
   				Path newPath = Path.fromString(mFileList[i].getAbsolutePath());
       			String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mFileList[i].getAbsolutePath().substring(mFileList[i].getAbsolutePath().lastIndexOf(".")+1).toLowerCase());
       			if (mFileList[i].isDirectory()){
       				newPath = Path.fromString("/data/data/com.android.gallery3d/files/directory.png");
       				String pathname;
       				if (currentPath.getParentFile() != null && currentPath.getParentFile().getAbsolutePath().contentEquals(mFileList[i].getAbsolutePath())) pathname="..";
       				else {
       					pathname=mFileList[i].getAbsolutePath();
       					if (pathname.length() > 1)
       						pathname=pathname.substring(pathname.lastIndexOf("/")+1);
       				}
       				list.add(new FilesystemImage(mApplication, newPath, "image/png", mFileList[i].getAbsolutePath(), pathname));
       			} else list.add(new FilesystemImage(mApplication, newPath, mime, null, null));
    		}
    		return list;
    	} else return null;
    }

    @Override
    public int getMediaItemCount() {
    	return mFileList.length;
    }

    @Override
    public String getName() {
    	// this wants the name of the ALBUM, aka filesystem path.
        return mName;
    }

    @Override
    public long reload() {
    	//TODO: should we bother watching the filesystem for changes to the open directory?
    	return mDataVersion;
    }

    @Override
    public int getSupportedOperations() {
        return /*SUPPORT_DELETE |*/ SUPPORT_SHARE | SUPPORT_INFO;
    }

    @Override
    public boolean isLeafAlbum() {
    	//TODO: return true when there are ZERO subdirectories to this directory
        return false;
    }
}
