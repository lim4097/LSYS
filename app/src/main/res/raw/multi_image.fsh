precision highp float;

uniform vec3                iResolution;
uniform sampler2D           iChannel1;
uniform sampler2D           iChannel0;
varying vec2                texCoord;

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	vec2 p = fragCoord.xy / iResolution.xy;
    
    // background    
    
    vec3 col = texture2D( iChannel1, p*3. ).xyz;

    fragColor = vec4(col*col+.25, 1.0 );
// Green screen part created by inigo quilez - iq/2015  
    vec3 fg = texture2D( iChannel0, (fract(p*3.)) ).xyz;
    
    //if (p.x<.5&&p.y<.5)
    {
    
    float maxrb = max( fg.r, fg.b );
    float k = clamp( (fg.g-maxrb)*3.0, .0, 1. );
    float dg = fg.g; 
    fg.g = min( fg.g, maxrb*0.8 ); 
    fg += dg - fg.g;
    //fg*=fg;
    fg+=.2;
    col = mix(fg, col, k);
    }
    
     fg = texture2D( iChannel0, p ).xyz;
    
    //if (p.x<.5&&p.y<.5)
    {
    float maxrb = max( fg.r, fg.b );
    float k = clamp( (fg.g-maxrb)*3.0, 0., 1.0 );
    float dg = fg.g; 
    fg.g = min( fg.g, maxrb*0.8 ); 
    fg += dg - fg.g;
    //fg*=fg;
    col = mix(fg, col, k);

    
    fragColor = vec4(col*col+.3, 1.0 );
    }
}

void main() {
	mainImage(gl_FragColor, texCoord * iResolution.xy);
}