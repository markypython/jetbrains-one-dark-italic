from collections import OrderedDict
import json
import os
import re
import shutil
import yaml
import xml.etree.cElementTree as ET
from xml.etree import ElementTree
from xml.etree.ElementTree import Element

SOURCE_DIR = os.path.join(os.path.dirname(__file__), 'config')
DEST_DIR = os.path.join(
    os.path.dirname(__file__),
    '..',
    'src',
    'main',
    'resources',
    'themes'
)
FILE_NAME = 'one_dark'


def build_theme_name(color: str, italic: bool):
    name = 'One Dark'

    if color != 'normal':
        name += ' ' + color

    if italic:
        name += ' italic'

    return name


class Builder:
    def __init__(self, color: str, italic: bool, filename: str):
        self.color = color
        self.italic = italic
        self.filename = filename

    def run(self) -> None:
        self.yaml = self.build_yaml()
        self.xml = self.build_xml()
        self.write_file()

    @staticmethod
    def read_yaml(filename: str) -> object:
        path = os.path.join(SOURCE_DIR, '%s.yaml' % filename)

        with open(path, 'r') as input_file:
            return yaml.load(input_file, Loader=yaml.FullLoader)

    def should_add_option(self, condition: str) -> bool:
        return condition == 'always' or (
            condition == 'theme' and self.italic is True
        )

    def get_color(self, color: str) -> str:
        for name, value in self.colors.items():
            if name == color:
                return value

        return color

    def build_yaml(self) -> dict:
        self.colors = self.read_yaml(os.path.join('colors', self.color))
        ide = self.read_yaml('ide')
        theme = self.read_yaml('theme')

        # Get a map of the ide options that points the option name
        #   to what type of style it is (i.e. font-type)
        ide_map = {
            option: style
            for style, options in ide.items()
            for option, _ in options.items()
        }

        for attribute, options in list(theme['attributes'].items()):
            # String-only options are the foreground color
            if isinstance(options, str):
                theme['attributes'][attribute] = {
                    'foreground': self.get_color(options)
                }

                continue

            saved_options = options.copy()
            for option, condition in list(options.items()):
                if option in ide_map:
                    # Remove the original option
                    del theme['attributes'][attribute][option]

                    # Add the actual JetBrains option if it applies to this theme
                    if self.should_add_option(condition):
                        key = ide_map[option]
                        value = ide[key][option]

                        theme['attributes'][attribute][key] = value
                else:
                    theme['attributes'][attribute][option] = self.get_color(condition)

            # If an option is both bold and italic, update the config
            if 'bold' in saved_options and \
                    'italic' in saved_options and \
                    self.should_add_option(saved_options['italic']):
                bold_italic = ide['font-type']['bold-italic']
                theme['attributes'][attribute]['font-type'] = bold_italic

        return theme

    def transform(self, text: str) -> str:
        return text.replace('-', '_').upper()

    def build_xml(self) -> ElementTree:
        scheme = ET.Element('scheme')

        scheme.attrib['name'] = build_theme_name(self.color, self.italic)
        scheme.attrib['parent_scheme'] = self.yaml['parent-scheme']
        scheme.attrib['version'] = '142'

        colors = ET.SubElement(scheme, 'colors')
        for name, value in self.yaml['colors'].items():
            ET.SubElement(colors, 'option', name=name, value=self.get_color(value))

        attributes = ET.SubElement(scheme, 'attributes')

        for attribute, base_attribute in self.yaml['inheriting-attributes'].items():
            ET.SubElement(
                attributes,
                'option',
                name=attribute,
                baseAttributes=base_attribute
            )

        for name, styles in self.yaml['attributes'].items():
            option = ET.SubElement(attributes, 'option', name=name)
            value = ET.SubElement(option, 'value')

            for style_name, style_value in styles.items():
                ET.SubElement(
                    value,
                    'option',
                    name=self.transform(style_name),
                    value=style_value
                )

        attributes[:] = sorted(attributes, key=lambda e: e.get('name'))

        return ET.ElementTree(scheme)

    def write_file(self) -> None:
        self.xml.write(os.path.join(DEST_DIR, self.filename))


def write_json(data: dict, output_path: str):
    with open(os.path.join(DEST_DIR, output_path + '.theme.json'), 'w') as output_file:
        json.dump(data, output_file)


def build_json():
    input_path = os.path.join(DEST_DIR, 'one_dark.theme.json')

    with open(input_path, 'r') as input_file:
        data = json.load(input_file, object_pairs_hook=OrderedDict)

    data['name'] = build_theme_name('normal', True)
    data['editorScheme'] = '/themes/one_dark_italic.xml'
    write_json(data, 'one_dark_italic')

    data['name'] = build_theme_name('vivid', False)
    data['editorScheme'] = '/themes/one_dark_vivid.xml'
    write_json(data, 'one_dark_vivid')

    data['name'] = build_theme_name('vivid', True)
    data['editorScheme'] = '/themes/one_dark_vivid_italic.xml'
    write_json(data, 'one_dark_vivid_italic')


def main():
    if not os.path.exists(DEST_DIR):
        os.makedirs(DEST_DIR)

    Builder('normal', False, '%s.xml' % FILE_NAME).run()
    Builder('normal', True, '%s_italic.xml' % FILE_NAME).run()
    Builder('vivid', False, '%s_vivid.xml' % FILE_NAME).run()
    Builder('vivid', True, '%s_vivid_italic.xml' % FILE_NAME).run()

    build_json()

    print('Theme files generated!')


if __name__ == '__main__':
    main()
