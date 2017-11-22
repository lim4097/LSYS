// --- trying simple tracking of lips.

// Red component is dominating the whole body.
// (col-min)/(Max-min) is normalized saturated color
// ( no luminance effect, 0 for blue, 1 for red )
// so that (col.g-min)/(Max-min) measures the orangeness: 0:red 1:yellow

precision highp float;

uniform vec3                iResolution;
uniform sampler2D           iChannel0;
varying vec2                texCoord;

void mainImage( out vec4 O, vec2 U )
{
    O = texture2D(iChannel0, U / iResolution.xy);
    float m = min(O.r,min(O.g,O.b)),
          M = max(O.r,max(O.g,O.b));
    
 // O = vec4(1.-(O.g-m)/(M-m)); // orangeness map
 //   O = mix(O, vec4(length(O.xyz)), smoothstep(.2,.4, (O.g-m)/(M-m)) );
  O = mix(O, vec4(length(O.xyz)), smoothstep(.0,.1, abs((O.g-m)/(M-m))-.11) ); // -.27
}

void main() {
	mainImage(gl_FragColor, texCoord * iResolution.xy);
}