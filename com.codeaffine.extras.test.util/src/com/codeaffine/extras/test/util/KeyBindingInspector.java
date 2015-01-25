package com.codeaffine.extras.test.util;

import static com.google.common.base.Objects.equal;

import java.util.Collection;

import com.codeaffine.eclipse.core.runtime.Extension;
import com.codeaffine.eclipse.core.runtime.Predicate;
import com.codeaffine.eclipse.core.runtime.RegistryAdapter;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;


public class KeyBindingInspector {

  public static final String DEFAULT_SCHEME_ID = "org.eclipse.ui.defaultAcceleratorConfiguration";

  private static final String BINDINGS_EP = "org.eclipse.ui.bindings";
  private static final String SEQUENCE = "sequence";
  private static final String PLATFORM = "platform";
  private static final String SCHEME_ID = "schemeId";
  private static final String COMMAND_ID = "commandId";
  private static final String CONTEXT_ID = "contextId";
  private static final String PARAMETER = "parameter";
  private static final String ID = "id";
  private static final String VALUE = "value";


  public static KeyBindingInfo keyBindingFor( String keySequence ) {
    return keyBindingFor( keySequence, null );
  }

  public static KeyBindingInfo keyBindingFor( String keySequence, String platform ) {
    Extension extension = readkeyBindingExtension( keySequence, platform );
    KeyBindingInfo result = null;
    if( extension != null ) {
      result = new KeyBindingInfo();
      result.setSchemeId( extension.getAttribute( SCHEME_ID ) );
      result.setCommandId( extension.getAttribute( COMMAND_ID ) );
      result.setContextId( extension.getAttribute( CONTEXT_ID ) );
      result.setPlatform( extension.getAttribute( PLATFORM ) );
      result.setParameters( getParameters( extension ) );
    }
    return result;
  }

  private static ParameterInfo[] getParameters( Extension extension ) {
    Collection<Extension> parameterElements = extension.getChildren( PARAMETER );
    ParameterElementToParameterInfo function = new ParameterElementToParameterInfo();
    Iterable<ParameterInfo> parameters = Iterables.transform( parameterElements, function );
    return Iterables.toArray( parameters, ParameterInfo.class );
  }

  private static Extension readkeyBindingExtension( String keySequence, String platform ) {
    return readKeyBindingExtension( new KeyBindingPredicate( keySequence, platform ) );
  }

  private static Extension readKeyBindingExtension( Predicate predicate ) {
    return new RegistryAdapter().readExtension( BINDINGS_EP ).thatMatches( predicate ).process();
  }

  private static class ParameterElementToParameterInfo implements Function<Extension, ParameterInfo>
  {
    @Override
    public ParameterInfo apply( Extension input ) {
      return new ParameterInfo( input.getAttribute( ID ), input.getAttribute( VALUE ) );
    }
  }

  private static class KeyBindingPredicate implements Predicate {
    private final String keySequence;
    private final String platform;

    KeyBindingPredicate( String keySequence, String platform ) {
      this.keySequence = keySequence;
      this.platform = platform;
    }

    @Override
    public boolean apply( Extension input ) {
      String keySequenceAttr = input.getAttribute( SEQUENCE );
      String platformAttr = input.getAttribute( PLATFORM );
      return equal( keySequence, keySequenceAttr ) && equal( platform, platformAttr );
    }
  }

}
