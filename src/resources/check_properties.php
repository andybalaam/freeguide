<?php
/**
*   Walks through all *.properties files and checks if all
*   the strings from the original file exist in the translated versions
*
*   @author Christian Weiske <cweiske@cweiske.de>
*/

$arCheck = array(
    'MessagesBundle', 
    'PrivacyBundle'
);


/**
*   returns all translatable string names from the properties file
*   @param  string  The properties file name
*/
function get_properties_strings($strFile)
{
    $arContent = file($strFile);
    $arStrings = array();
    foreach ($arContent as $strLine) {
        $nPos = strpos($strLine, '=');
        if ($nPos !== false) {
            $arStrings[] = substr($strLine, 0, $nPos);
        }
    }
    return $arStrings;
}//function get_properties_strings($strFile)



foreach ($arCheck as $strBase) {
    echo 'Checking ' . $strBase . "\r\n";

    //load original file
    $strOriginal = $strBase . '.properties';
    if (!file_exists($strOriginal)) {
        echo ' Original file ' . $strOriginal . ' doesn\'t exist' . "\r\n";
        continue;
    }
    
    $arOriginal = get_properties_strings($strOriginal);
    
    //loop through all translated files
    $arTranslated   = array();
    $hdlDir = dir('.');
    while (false !== ($strFile = $hdlDir->read())) {
        if (preg_match('/' . $strBase . '[a-zA-Z_]+\\.properties/', $strFile)) {
            $arTranslated[$strFile] = get_properties_strings($strFile);
            
            $arDiff = array_diff($arOriginal, $arTranslated[$strFile]);
            if (count($arDiff) > 0) {
                echo ' ' . $strFile . ' misses the follwing strings:' . "\r\n";
                foreach( $arDiff as $strString) {
                    echo '  ' . $strString . "\r\n";
                }
                
            }//diffed result is there
            else {
                echo ' ' . $strFile . ' ok.' . "\r\n";
            }
        }//filename matches
    }//while all files
    
    
}//foreach $arCheck

?>