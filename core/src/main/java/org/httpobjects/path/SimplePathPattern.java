/**
 * Copyright (C) 2011, 2012 Commission Junction Inc.
 *
 * This file is part of httpobjects.
 *
 * httpobjects is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * httpobjects is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with httpobjects; see the file COPYING.  If not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */
package org.httpobjects.path;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class SimplePathPattern implements PathPattern {
    private final String pattern;
    private final List<Seg> segs = new ArrayList<Seg>();
    
    public SimplePathPattern(String pattern) {
        super();
        this.pattern = pattern;
        
        String[] segments = pattern.split(Pattern.quote("/"));
        
        for(String next : segments){
            if(next.startsWith("{")){
                if(next.endsWith("*}")){
                    segs.add(new Seg(next.substring(1, next.length()-2), true, true));
                }else{
                    segs.add(new Seg(next.substring(1, next.length()-1), true, false));
                }
            }else{
                segs.add(new Seg(next, false, false));
            }
        }
        
    }
    
    public List<PathParamName> varNames(){
        List<PathParamName> names = new ArrayList<PathParamName>(segs.size());
        for(Seg s:segs){
            if(s.isVar) names.add(s.name);
        }
        return names;
    }
    
    public boolean matches(String path) {
        final boolean v = match(path)!=null;
        return v;
    }
    
    private void stripQueryString(String[] segments){
        if(segments.length>0){
            final int x = segments.length -1;
            final String s = segments[x];
            final int index = s.lastIndexOf('?');
            if(index!=-1){
                segments[x] = s.substring(0, index);
            }
            
        }
    }
    
    public Path match(String path){
        if(path == null) return null;
        
        String[] segments = path.split(Pattern.quote("/"));
        stripQueryString(segments);
        
        List<PathParam> params = new LinkedList<PathParam>();
        boolean matches = true;
        
        Seg lastSeg = null;
        String wildcardMatch = "";
        
        int limit = Math.max(segs.size(), segments.length);
        
        for(int x=0;x<limit;x++){
            Seg seg = x<segs.size()?segs.get(x):null;
            String s = x<segments.length?segments[x]:null;
            
            if(seg==null && (lastSeg==null || !lastSeg.hasWildcard)){
                matches = false;
                break;
            }else if(lastSeg!=null && lastSeg.hasWildcard){
                if(s.length()>0 && s!=null){
                    wildcardMatch += "/";
                }
                wildcardMatch += s;
            }else if(seg!=null){
                if(seg.isVar){
                    if(seg.hasWildcard && s!=null){
                        wildcardMatch += s;
                    }else{
                        params.add(seg.name.withValue(s));
                    }
                }else if(!seg.name.equals(s) && !seg.name.equals("*")){
                    matches = false;
                }
            }
            if(seg!=null) lastSeg = seg;
        }
        
        if(lastSeg!=null && lastSeg.hasWildcard && !wildcardMatch.isEmpty()){
            params.add(lastSeg.name.withValue(wildcardMatch));
        }
        
        if(matches){
            return new Path(path, params.toArray(new PathParam[]{}));
        }else{
            return null;
        }
    }
    
    public String raw() {
        return pattern;
    }
    
    private static class Seg {
        final PathParamName name;
        final boolean isVar;
        final boolean hasWildcard;
        
        private Seg(String name, boolean isVar, boolean hasWildcard) {
            super();
            this.name = new PathParamName(name);
            this.isVar = isVar;
            this.hasWildcard = hasWildcard;
        }
        
        @Override
        public String toString() {
            return name + " (isVar=" + isVar + ", hasWildcard=" + hasWildcard + ")";
        }
    }
}
