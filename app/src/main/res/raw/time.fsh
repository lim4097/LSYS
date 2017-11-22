precision highp float;

uniform vec3                iResolution;
uniform float               iGlobalTime;
uniform sampler2D           iChannel0;
uniform sampler2D           iChannel1;
uniform sampler2D           iChannel2;
uniform sampler2D           iChannel3;
varying vec2                texCoord;

#define FUDGE_AMOUNT 0.06


void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 screenCoord = fragCoord/iResolution.xy;
    vec2 fuckCoord = screenCoord;
    float fuckAmt = texture2D(iChannel3, vec2(cos(iGlobalTime*12.), sin(iGlobalTime*16.))).r;
    fuckAmt = pow(fuckAmt, 2.);
    fuckCoord.x += (sin(screenCoord.x*pow(iGlobalTime,2.7))*fuckAmt)/64.;
    fuckCoord.y += (cos(screenCoord.y*pow(iGlobalTime,2.3))*fuckAmt)/56.;

    //fuckCoord = mix(fuckCoord, ceil(fuckCoord), sin(iGlobalTime));
    
    vec4 co = texture2D( iChannel0, fuckCoord);
    vec4 oc = texture2D( iChannel1, screenCoord);
    vec4 test = mix(co, oc, co.g);
    if(co.g > 0.005 && ((co.g > co.r + FUDGE_AMOUNT) && (co.g > co.b + FUDGE_AMOUNT))){
        test = oc;
        //test = vec4(0., 0., 0., 1.);
    }
    else{
        vec4 noise = texture2D(iChannel3, screenCoord);
        noise.r = pow(noise.r, 2.);
        noise.g = pow(noise.g, 2.);
        noise.b = pow(noise.b, 2.);
        test = mix(co*oc*texture2D(iChannel2, vec2(screenCoord.x+(iGlobalTime/5.), screenCoord.y))*noise, oc, co.r);
    }
    fragColor = test;
}

void main() {
	mainImage(gl_FragColor,  texCoord * iResolution.xy);
}