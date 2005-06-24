<?php
/**
*   Walks through all *.properties files and checks if all
*   the strings from the original file exist in the translated versions
*
*   it it capable of checking mnemonic groups for uniqueness as well
*
*   @author Christian Weiske <cweiske@cweiske.de>
*/

$arCheck = array(
    'MessagesBundle' => 'MessagesBundle.en.properties',
    'Startup' => 'Startup.en.properties'
);
$arIgnore = array(
	'MessagesBundle.en_US.properties'
);

#something like .utf-8
$strAdditionalExtension = '';

$arMnemonicGroups = array (
/*	//main dialog
	'main dialog' => array(
		'bdr.main.mnemonic.ok',
		'bdr.main.mnemonic.openother',
		'bdr.main.mnemonic.edit',
		'bdr.main.mnemonic.options',
		'bdr.main.mnemonic.help',
		'bdr.main.mnemonic.lblFile',
		'bdr.main.mnemonic.lblTitle',
		'bdr.main.mnemonic.lblAuthor',
		'bdr.main.mnemonic.lblVersion'
	),
	//edit dialog
	'edit dialog' => array(
		'bdr.edit.mnemonic.import',
		'bdr.edit.mnemonic.export',
		'bdr.edit.mnemonic.save',
		'bdr.edit.mnemonic.cancel',
		'bdr.edit.mnemonic.add',
		'bdr.edit.mnemonic.remove',
		'bdr.edit.mnemonic.look',
		'bdr.main.mnemonic.lblFile',
		'bdr.main.mnemonic.lblTitle',
		'bdr.main.mnemonic.lblAuthor',
		'bdr.main.mnemonic.lblVersion'
	),
	//options
	'options' => array(
		'bdr.options.mnemonic.cancel',
		'bdr.options.mnemonic.save',
		'bdr.options.mnemonic.dateformat',
		'bdr.options.mnemonic.language',
		'bdr.options.mnemonic.runonce',
		'bdr.options.mnemonic.autoclose',
		'bdr.options.mnemonic.ok2tray',
	),
	//help
	'help' => array(
		'bdr.about.mnemonic.close'
	),
	//export dialog
	'export' => array(
		'bdr.main.mnemonic.lblTitle',
		'bdr.main.mnemonic.lblAuthor',
		'bdr.main.mnemonic.lblVersion',
		'bdr.port.mnemonic.btnSelectAll',
		'bdr.port.mnemonic.btnSelectNone',
		'bdr.port.mnemonic.btnSynchronize',
		'bdr.edit.mnemonic.export',
		'bdr.edit.mnemonic.cancel'
	),
	//import dialog
	'import' => array(
		'bdr.main.mnemonic.lblTitle',
		'bdr.main.mnemonic.lblAuthor',
		'bdr.main.mnemonic.lblVersion',
		'bdr.port.mnemonic.btnSelectAll',
		'bdr.port.mnemonic.btnSelectNone',
		'bdr.port.mnemonic.btnSynchronize',
		'bdr.edit.mnemonic.import',
		'bdr.edit.mnemonic.cancel'
	),
*/
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
            $arStrings[] = trim(substr($strLine, 0, $nPos));
        }
    }
    return $arStrings;
}//function get_properties_strings($strFile)



foreach ($arCheck as $strBase => $strOriginal) {
    echo 'Checking ' . $strBase . "\r\n";

    //load original file
//    $strOriginal = $strBase . '.properties' . $strAdditionalExtension;
    if (!file_exists($strOriginal)) {
        echo ' Original file ' . $strOriginal . ' doesn\'t exist' . "\r\n";
        continue;
    }

    $arOriginal = get_properties_strings($strOriginal);

    //loop through all translated files
    $arTranslated   = array();
    $hdlDir = dir('.');
    while (false !== ($strFile = $hdlDir->read())) {
        if (preg_match('/' . $strBase . '\\.[a-zA-Z_]+\\.properties' . str_replace('.', '\\.', $strAdditionalExtension) . '/', $strFile)
            && !in_array($strFile, $arIgnore)) {
            $arTranslated[$strFile] = get_properties_strings($strFile);

            $arDiff = array_diff($arOriginal, $arTranslated[$strFile]);
            if (count($arDiff) > 0) {
                echo ' ' . $strFile . ' misses the following strings:' . "\r\n";
                foreach( $arDiff as $strString) {
                    echo '  ' . $strString . "\r\n";
                }

            }//diffed result is there
            else {
                echo ' ' . $strFile . ' ok.' . "\r\n";
            }
/*			Problem: arTranslated[strFile] doesn't have key=>value pairs, only number=>key pairs.
			if (isset($arMnemonicGroups)) {
				//check if all mnemonics in a group are unique
				foreach ($arMnemonicGroups as $strGroupName => $arMnemonics) {
					$arUsedMnemonics = array();
					foreach ($arMnemonics as $strMnemonic) {
						if (isset($arTranslated[$strFile][$strMnemonic])) {
							$arUsedMnemonics[$strMnemonic]	= strtolower($arTranslated[$strFile][$strMnemonic]);
						}
					}
					$arUnique = array_unique($arUsedMnemonics);
					if (count($arUsedMnemonics) > count($arUnique)) {
						//something's going on here!
						echo '  Mnemonic problem in group ' . $strGroupName . ":\r\n";
						$arDiff = array_diff_assoc($arUsedMnemonics, $arUnique);
						var_dump($arDiff);
						foreach ($arDiff as $strId => $strMnemonic) {
							echo '    ' . $strMnemonic . ' - ' . $strId . "\r\n";
						}
					}
				}
			}
*/
        }//filename matches
    }//while all files


}//foreach $arCheck
?>