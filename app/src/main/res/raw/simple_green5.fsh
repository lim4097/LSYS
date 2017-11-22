precision highp float;

uniform vec3                iResolution;
uniform float               iGlobalTime;
uniform sampler2D           iChannel0;
uniform sampler2D           iChannel1;
varying vec2                texCoord;

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 xy = fragCoord.xy/iResolution.xy;

    vec4 texColor = texture2D(iChannel0, xy);
    vec4 tex2Color = texture2D(iChannel1, xy);
    /*
    if ((texColor.r+texColor.b)<texColor.g){
     texColor.rgb = tex2Color.rgb;
    }*/
    vec4 backgroundColor = vec4(13.0/255.0,171.0/255.0,17.0/255.0,1.0); //  13.0, 161.0, 37.0
	float difference = distance(texColor,backgroundColor); //calculates the difference between two vectors
	if( difference < 0.45 ) {  //0.4

		texColor.rgb = tex2Color.rgb;

	}

    fragColor = texColor;

    //texColor.rgb *= abs(sin(iGlobalTime)); //fade in and out to black

    //fragColor = texColor;
}

void main() {
	mainImage(gl_FragColor,  texCoord * iResolution.xy);
}