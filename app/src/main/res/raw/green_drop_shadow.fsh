precision highp float;

uniform vec3                iResolution;
uniform float               iGlobalTime;
uniform sampler2D           iChannel0;
uniform sampler2D           iChannel1;
varying vec2                texCoord;

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	vec2 uv = fragCoord.xy / iResolution.xy;
	fragColor = texture2D(iChannel1, uv);
    
    vec4 jcvd = texture2D(iChannel0,uv-vec2(.01,-.02));
    fragColor.xyz *= 1. - (.5 * jcvd.a);
    
   	jcvd = texture2D(iChannel0, uv);
    fragColor.xyz = mix(fragColor.xyz, jcvd.xyz, jcvd.a);

}

void main() {
	mainImage(gl_FragColor,  texCoord * iResolution.xy);
}