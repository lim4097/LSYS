precision highp float;

uniform vec3                iResolution;
uniform float               iGlobalTime;
uniform sampler2D           iChannel0;
uniform sampler2D           iChannel1;
varying vec2                texCoord;

// Created by inigo quilez - iq/2013
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.

#define METHOD 2

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	vec2 q = fragCoord.xy / iResolution.xy;

	vec3 bg = texture2D( iChannel0, q ).xyz;
	vec3 fg = texture2D( iChannel1, q ).xyz;


    float maxrb = max( fg.r, fg.b );
    float k = clamp( (fg.g-maxrb)*5.0, 0.0, 1.0 );

#if METHOD==1

	float ll = length( fg );
    fg.g = min( fg.g, maxrb*0.8 );
    fg = ll*normalize(fg);

#else

    float dg = fg.g;
    fg.g = min( fg.g, maxrb*0.8 );
    fg += dg - fg.g;

#endif

    fragColor = vec4( mix(fg, bg, k), 1.0 );
}


void main() {
	mainImage(gl_FragColor,  texCoord * iResolution.xy);
}