var base_url = "http://www.artificialworlds.net/freeguide/";

function freeguide_redirect( normal_redirect_location, anchor_redirects )
{
	var output_url;
	var input_url = document.URL;
	var anchor_idx = input_url.lastIndexOf( '#' );

	output_url = base_url + normal_redirect_location

	// If we are looking for a specific part of this page, redirect somewhere else
	if( anchor_redirects&& anchor_idx != -1 )
	{
		var anchor_text = input_url.substr( anchor_idx + 1 );
		var specific_redirect = anchor_redirects[anchor_text];
	
		if( specific_redirect )
		{
			output_url = base_url + specific_redirect;
		}
	}

	document.location = output_url;

}
