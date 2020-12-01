package com.google.inject.tools.ideplugin.eclipse;

import java.util.Collections;
import java.util.Set;

import com.google.inject.Singleton;
import com.google.inject.tools.ideplugin.CustomContextDefinitionSource;
import com.google.inject.tools.ideplugin.JavaProject;
import com.google.inject.tools.ideplugin.Source;
import com.google.inject.tools.ideplugin.Source.SourceListener;
import com.google.inject.tools.suite.ProgressHandler.ProgressMonitor;

@Singleton
public class NoOpCustomContextDefinitionSource
    implements CustomContextDefinitionSource
{
    public void addListener( SourceListener listener )
    {
    }

    public void removeListener( SourceListener listener )
    {
    }

    public Set<String> get( JavaProject javaProject, ProgressMonitor progressMonitor )
    {
        return Collections.emptySet();
    }

    public boolean isListeningForChanges()
    {
        return false;
    }

    public void listenForChanges( boolean listenForChanges )
    {
    }
}
